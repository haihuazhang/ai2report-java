const fs = require('fs');
fs.writeFileSync('./default-env.json', process.env.VCAP_SERVICES);
fs.writeFileSync('./app/router/default-services.json', JSON.stringify({ "uaa": JSON.parse(process.env.VCAP_SERVICES).xsuaa[0].credentials }));