module.exports = function(app, tokenMap){
	console.log('loading heartbeat data functions');
	var mongo = require('mongodb');
	var Server = mongo.Server;
	var Db = mongo.Db;
	var _ = require('underscore');
	var tm = tokenMap;

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
								'start_time':startTime, 'endTime':endTim,
								'data':data});
							}
						);
						}
					}
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