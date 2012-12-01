
module.exports = function(app){
var mongo = require('mongodb');
var Server = mongo.Server;
var Db = mongo.Db;
var uuid = require('node-uuid');
console.log('loading users module');

//user routes
app.get('/', function( req, res ){
  res.send( 'Hello World' );
});



app.post( '/users/create', function ( req, res ) {
  var post = req.body;
  var userName = post.user;
  var password = post.password;

  var userRecord = {"user_name" : userName, "password" : password};
  console.log("Attempting to create a new user");
  console.log(userRecord);
  var server = new Server('localhost', 27017, {auto_reconnect: true});
  var db = new Db('beat_keeper', server);
  db.open(function(err, db){
      db.collection('users', function(err, collection){
      collection.insert(userRecord);
    });
  });
});

app.get('/users/create', function(req, res){
   var html = '<form method="post" action="/users/create" enctype="multipart/form-data">';
    html +=  '<p><input type="text" name="user" /></p>';
    html +=  '<p><input type="text" name="password" /></p>';
    html +=  '<p><input type="submit" value="Upload" /></p>';
    html +=  '</form>';
    res.setHeader('Content-Type', 'text/html');
    res.setHeader('Content-Length', Buffer.byteLength(html));
    res.end(html);
});

app.post('/users/login', function (req, res) {
  var post = req.body;
  console.log('Attempting to loging user');
  console.log(post);
  console.log(post.user);
  var server = new Server('localhost', 27017, {auto_reconnect: true, safe:false});
  var db = new Db('beat_keeper', server);
  db.open(function(err, db){
      db.collection('users', function(err, collection){
        collection.findOne({"user_name":post.user}, function(err, result){
          if(!err){
            if(result['password'] === post.password){
              console.log("login worked!");
              req.session.user_id = result['user_name'];
              res.redirect('/my_secret_page');
            }
          }else{
            var html = 'Your username could not be found';
            res.setHeader('Content-Type', 'text/html');
            res.setHeader('Content-Length', Buffer.byteLength(html));
            res.end(html);
          }
        });
    });
  });
});

  app.post('/users/login_token', function (req, res){
      var post = req.body;
      console.log('Attempting to loging user');
      console.log(post);
      console.log(post.user_name);
      console.log(post.password);
      var server = new Server('localhost', 27017, {auto_reconnect: true});
      var db = new Db('beat_keeper', server);
      db.open(function(err, db){
          db.collection('users', function(err, collection){
            collection.findOne({"user_name":post.user}, function(err, result){
              if(!err){
                if(result === null) console.log("Error result is null");
                if(result['password'] === post.password){
                  console.log("login worked!");
                  var token = uuid.v1();
                  req.session.token = {"token":token,"user_name":result['user_name']};
                  res.contentType('json');
                  res.send({session_token:token});
                }
              }else{
                var html = 'Your username could not be found';
                res.setHeader('Content-Type', 'text/html');
                res.setHeader('Content-Length', Buffer.byteLength(html));
                res.end(html);
              }
            });
        });
  });

  /*if (post.user == 'test_user' && post.password == 'test_password') {
    req.session.user_id = 'john';
    console.log("login worked!");
    res.redirect('/my_secret_page');
  } else {
    res.send('Bad user/pass \r\n');
  }*/
});

app.get('/users/login', function(req, res){ 
  var html = '<form method="post" action="/users/login_token" enctype="multipart/form-data">'
      html +=  '<p><input type="text" name="user" /></p>'
      html +=  '<p><input type="text" name="password" /></p>'
      html +=  '<p><input type="submit" value="Upload" /></p>'
    html +=  '</form>';
  res.setHeader('Content-Type', 'text/html');
    res.setHeader('Content-Length', Buffer.byteLength(html));
    res.end(html);
  });

app.get( '/users/read/:id', function ( req, res ) {
  res.contentType( 'json' );
  res.send( { title: 'User with id ' + req.params.id + ' found' } );
});

app.post( '/users/update/:id([0-9]+)', function ( req, res ) {
  res.contentType( 'json' );
  res.send( { title: 'User with id ' + req.params.id + ' updated' } );
});
app.get( '/users/delete/:id([0-9]+)', function ( req, res ) {
  res.contentType( 'json' );
  res.send( { title: 'User with id ' + req.params.id + ' deleted' } );
});

app.all( '/users/*?', function ( req, res, next ) {
  res.contentType( 'json' );
  next();
});
/*
function objectify ( str ) {
    str = str.slice( 0, -1 );
    str = str.charAt( 0 ).toUpperCase() +
      str.substring( 1 ).toLowerCase();
return str; }*/
};