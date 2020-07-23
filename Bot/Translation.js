//A text representation of my Google Scripts webapp. This file has no use itself but the code is the same as the webapp which Translation.java sends a request to to get the translation
// This file was copied in full from a reply sent by StackExchange user Maksym Pecheniuk at https://stackoverflow.com/questions/8147284/how-to-use-google-translate-api-in-my-java-application


var mock = {
  parameter:{
    q:'hello',
    source:'en',
    target:'fr'
  }
};


function doGet(e) {
  e = e || mock;

  var sourceText = ''
  if (e.parameter.q){
    sourceText = e.parameter.q;
  }

  var sourceLang = '';
  if (e.parameter.source){
    sourceLang = e.parameter.source;
  }

  var targetLang = 'en';
  if (e.parameter.target){
    targetLang = e.parameter.target;
  }

  var translatedText = LanguageApp.translate(sourceText, sourceLang, targetLang);
  
  return ContentService.createTextOutput(translatedText).setMimeType(ContentService.MimeType.JSON);
}
