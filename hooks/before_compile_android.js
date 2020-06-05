const path = require("path"),
    fs = require("fs"),
    xmlDom = require("xmldom"),
    cordovaRoot = path.resolve(),
    DELIVERY_SERVICE_PATH = "./platforms/android/app/src/main/java/com/android/plugins/DeliveryService.java";

module.exports = function () {
    let DOMParser = xmlDom.DOMParser,
        cordovaPushDeliveryConfig = path.resolve(cordovaRoot, 'config.xml'),
        xmlContent = fs.readFileSync(cordovaPushDeliveryConfig, 'utf8'),
        document = new DOMParser().parseFromString(xmlContent, 'text/xml'),
        variables = document.getElementsByTagName('variable'),
        deliveryUrlKey = 'delivery_url';

    for (let i = 0; i < variables.length; i++) {
        let nameAttribute = variables[i].getAttribute("name");
        if (nameAttribute === "DELIVERY_URL_KEY") {
            deliveryUrlKey = variables[i].getAttribute("value");
        }
    }

    let deliveryService = fs.readFileSync(DELIVERY_SERVICE_PATH).toString();
    deliveryService = deliveryService
        .replace(new RegExp('\key = "(.+?)\"'), 'key = "' + deliveryUrlKey + '"')
        .replace(new RegExp('\has `(.+?)\`'), 'has `' + deliveryUrlKey + '`');
    fs.writeFileSync(DELIVERY_SERVICE_PATH, deliveryService, 'utf8');
};
