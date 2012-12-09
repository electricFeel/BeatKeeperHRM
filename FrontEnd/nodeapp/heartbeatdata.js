module.exports = function(app, tokenMap){
	console.log('loading heartbeat data functions');
	var mongo = require('mongodb');
	var Server = mongo.Server;
	var Db = mongo.Db;
	var _ = require('underscore');
	var tm = tokenMap;

	var mongoAddy = 'ec2-50-17-145-141.compute-1.amazonaws.com/beat_keeper';

	app.post('/hbdata/add', function(req, res){
		var post = req.body;
		var token = post.session_token;
		var data = post.data;
		var startTime = parseDate(post.start_time);
		var endTime = parseDate(post.end_time);



		var server = new Server('localhost', 27017, {auto_reconnect: true});
		var db = new Db('beat_keeper', server);

		db.open(function(err, db){
			db.collection('users', function(err, collection){
				collection.findOne({'token':token}, function(err, result){
					if(!err){
						if(result !== null){
							var userName = result['user_name'];
							db.collection('beat_data', function(err, beatData){
								beatData.insert({'user_name':userName,
								'start_time':startTime, 'endTime':endTime,
								'data':data});
							}
						);
						}
					}
				});
			});
		});
	});

	app.get('/hbdata/serve', function(req, res){
		if(!req.session.user_id)
		{
			res.render('login.ejs',{loggedIn:false, userName:''});
		}
		var server = new Server('localhost', 27017, {auto_reconnect: true});
		var db = new Db('beat_keeper', server);
		db.open(function(err,db){
			db.collection('beat_data', function(err, collection){
				if(err) console.log('error found');
					collection.find({'user_name':req.session.user_id}, function(err, cursor){
					//Get the dates of the first and last item in the collection and order the data
					//Stringify the data
					cursor.toArray(function(err, items){
					var dateRanges = [];
					for(var i = 0; i < items.length; i++){
						date.push(items[i]['start_time'], items[i]['endTime']);
					}
					
					//Provide it to the front end.
					res.render('data.ejs',{
						loggedIn:true,
						userName:req.session.user_id,
						dateRanges : dateRanges,
						title: 'Heart beat data'
					});
					});
				});
			});
		});
	});


	var parseDate = function(dateText){
		var year = dateText.slice(0,4);
		var month = dateText.slice(4,6);
		var day = dateText.slice(6,8);
		var hour = dateText.slice(9,11);
		var min = dateText.slice(11,13);
		var sec = dateText.slice(13,15);
		var d = new Date(year, month, day, hour, min, sec, 0);
		return d;
	};
};