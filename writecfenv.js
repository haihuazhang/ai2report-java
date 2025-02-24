const fs = require('fs');
fs.writeFileSync('./default-env.json',process.env.VCAP_SERVICES);
// fs.writeFileSync('./app/router/default-env.json',process.env.VCAP_SERVICES);