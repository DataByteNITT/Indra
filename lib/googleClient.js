/**
 * Indra Backend
 * 
 * Mohammed Sohail <sohailsameja@gmail.com>
 * Released under the AGPL-3.0 License
 **/


exports = module.exports = {
    getSyntax,
    getEntities
}


// node modules
const language = require("@google-cloud/language");


// module variables
const googleNlpClient = new language.LanguageServiceClient({ keyFilename: "./credentials.json" });


// methods
function getEntities(input, callback) {

    const docObject = {
        content: input,
        type: "PLAIN_TEXT"
    };

    googleNlpClient.analyzeEntities({ document: docObject }).then(ctx => {
        return callback(null, ctx);
    }).catch(error => {
        return callback(error);
    });
}

function getSyntax(input, callback) {

    const docObject = {
        content: input,
        type: "PLAIN_TEXT"
    };

    googleNlpClient.analyzeSyntax({ document: docObject }).then(ctx => {
        return callback(null, ctx);
    }).catch(error => {
        return callback(error);
    });
}