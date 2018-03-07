/**
 * Indra Backend
 * 
 * Mohammed Sohail <sohailsameja@gmail.com>
 * Released under the AGPL-3.0 License
 **/


exports = module.exports = {
    execParallel
}


// node modules
const _ = require("lodash");
const getMyKeys = require("uas-get-my-keys");
const Parallel = require("fastparallel");
const request = require("request-promise");


// own modules
const gClient = require("./googleClient");


// module variables
const parallel = Parallel({ results: true });
let result;


// method function
function execParallel(inputString) {
    parallel({}, [gClient.getSyntax, gClient.getEntities], inputString, parseText);
}

function parseText(error, results) {
    const tokensObject = getMyKeys(results, ["tokens"]);
    const entitiesObject = getMyKeys(results, ["entities"]);

    const entitiesArray = entitiesObject.entities;
    const entityArray = [];

    const tokensArray = tokensObject.tokens;
    const syntaxArray = [];

    const words = [];

    for (let i = 0; i < entitiesArray.length; i++) {
        const obj = {};

        if (!_.isEmpty(entitiesArray[i].metadata)) {
            object = {
                word: entitiesArray[i].name,
                word_type: entitiesArray[i].mentions[0].type,
                offset: entitiesArray[i].mentions[0].text.beginOffset,
                type: entitiesArray[i].type,
                salience: entitiesArray[i].salience,
                wikipedia_url: true
            }
        } else {
                object = {
                    word: entitiesArray[i].name,
                    word_type: entitiesArray[i].mentions[0].type,
                    offset: entitiesArray[i].mentions[0].text.beginOffset,
                    type: entitiesArray[i].type,
                    salience: entitiesArray[i].salience,
                    wikipedia_url: false
                }           
        }
        
        entityArray.push(object);
    }

    for (let i = 0; i < tokensArray.length; i++) {
        
        object = {
            word: tokensArray[i].text.content,
            dependencyEdgeTokenIndex: tokensArray[i].dependencyEdge.headTokenIndex,
            tag: tokensArray[i].partOfSpeech.tag
        }

        words.push(tokensArray[i].text.content);
        syntaxArray.push(object);
    }

    const treeObject = {
        tree: []
    };

    for (let i = 0; i < words.length; i++) {
        if (words[syntaxArray[i].dependencyEdgeTokenIndex] === words[i]) {
            treeObject.root = words[i];
        } else {
            treeObject.tree.push([
                words[syntaxArray[i].dependencyEdgeTokenIndex],
                words[i]
            ]);
        }            
    }

    const resultObject = {payload:{
        entities_analysis: entityArray,
        syntax_analysis: syntaxArray,
        dependency_tree: [treeObject]
    }};    

    const postOptions = {
        method: "POST",
        uri: "http://127.0.0.1:5000/hello",
        headers: {
            "content-type": "application/json"
        },
        body: resultObject,
        json: true
    }

    request(postOptions);
}