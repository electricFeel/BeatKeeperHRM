var restify = require('./node_modules/restify/');
var mongodb = require('./node_modules/mongoskin/');
var server = restify.createServer();

function respond(req, res, next) {
  res.send('hello ' + req.params.name);
}

 function send(req, res, next) {
   res.send('hello ' + req.params.name);
   return next();
 }
 
 function putName(req, res, next) {
   res.send('puttings ' + req.params.name + '   ' + req.params.id);
   return next();
 }
 
 function getName(req, res, next){
     req.send('getting ' + req.params.name);
     return next();
 }

 server.post('/hello', function create(req, res, next) {
   res.send(201, Math.random().toString(36).substr(3, 8));
   return next();
 });
 
 server.put('/hello', getName);
 server.get('/hello/:name/:id', putName);
 server.head('/hello/:name', send);
 server.del('hello/:name', function rm(req, res, next) {
   res.send(204);
   return next();
 });
server.listen(8080, function() {
  console.log('%s listening at %s', server.name, server.url);
});