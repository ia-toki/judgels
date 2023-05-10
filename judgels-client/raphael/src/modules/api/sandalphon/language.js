// extracted from https://github.com/anurbol/languages-iso-639-1-2-3-json/blob/master/data.json
export const languageNamesMap = {
  af: 'Afrikaans',
  ak: 'Akan',
  am: 'Amharic',
  ar: 'Arabic',
  as: 'Assamese',
  az: 'Azerbaijani',
  be: 'Belarusian',
  bg: 'Bulgarian',
  bm: 'Bambara',
  bn: 'Bengali',
  bo: 'Tibetan',
  br: 'Breton',
  bs: 'Bosnian',
  ca: 'Catalan',
  cs: 'Czech',
  cy: 'Welsh',
  da: 'Danish',
  de: 'German',
  dz: 'Dzongkha',
  el: 'Greek',
  en: 'English',
  es: 'Spanish',
  et: 'Estonian',
  eu: 'Basque',
  fa: 'Persian',
  fi: 'Finnish',
  fo: 'Faroese',
  fr: 'French',
  ga: 'Irish',
  gd: 'Scottish Gaelic',
  gl: 'Galician',
  gu: 'Gujarati',
  gv: 'Manx',
  he: 'Hebrew',
  hi: 'Hindi',
  hr: 'Croatian',
  hu: 'Hungarian',
  hy: 'Armenian',
  id: 'Indonesian',
  ig: 'Igbo',
  ii: 'Sichuan Yi',
  is: 'Icelandic',
  it: 'Italian',
  ja: 'Japanese',
  ka: 'Georgian',
  ki: 'Kikuyu',
  kk: 'Kazakh',
  kl: 'Kalaallisut',
  km: 'Khmer',
  kn: 'Kannada',
  ko: 'Korean',
  ks: 'Kashmiri',
  kw: 'Cornish',
  ky: 'Kyrgyz',
  lb: 'Luxembourgish',
  lg: 'Ganda',
  lo: 'Lao',
  lt: 'Lithuanian',
  lu: 'Luba-Katanga',
  lv: 'Latvian',
  mg: 'Malagasy',
  mk: 'Macedonian',
  ml: 'Malayalam',
  mn: 'Mongolian',
  mr: 'Marathi',
  ms: 'Malay',
  mt: 'Maltese',
  my: 'Burmese',
  nb: 'Norwegian Bokmål',
  nd: 'North Ndebele',
  ne: 'Nepali',
  nl: 'Dutch',
  or: 'Oriya',
  pa: 'Punjabi',
  pl: 'Polish',
  ps: 'Pashto',
  pt: 'Portuguese',
  rm: 'Romansh',
  rn: 'Rundi',
  ro: 'Romanian',
  ru: 'Russian',
  rw: 'Kinyarwanda',
  sg: 'Sango',
  si: 'Sinhala',
  sk: 'Slovak',
  sl: 'Slovenian',
  sn: 'Shona',
  sq: 'Albanian',
  sr: 'Serbian',
  sv: 'Swedish',
  sw: 'Swahili',
  ta: 'Tamil',
  te: 'Telugu',
  th: 'Thai',
  ti: 'Tigrinya',
  to: 'Tongan',
  tr: 'Turkish',
  ug: 'Uyghur',
  uk: 'Ukrainian',
  ur: 'Urdu',
  uz: 'Uzbek',
  vi: 'Vietnamese',
  yi: 'Yiddish',
  zh: 'Chinese',
  'zh-Hans': 'Chinese',
  'zh-Hans-CN': 'Chinese',
  'zh-Hans-HK': 'Chinese',
  'zh-Hans-MO': 'Chinese',
  'zh-Hans-SG': 'Chinese',
  'zh-Hant': 'Chinese',
  'zh-Hant-HK': 'Chinese',
  'zh-Hant-MO': 'Chinese',
  'zh-Hant-TW': 'Chinese',
  zu: 'Zulu',
};

export const languageDisplayNamesMap = Object.assign(
  {},
  ...Object.keys(languageNamesMap).map(code => ({
    [code]: languageNamesMap[code] + ' (' + code + ')',
  }))
);

export function sortLanguagesByName(languages) {
  return languages.slice().sort((a, b) => {
    const nameA = languageNamesMap[a];
    const nameB = languageNamesMap[b];
    return nameA < nameB ? -1 : nameA > nameB ? 1 : 0;
  });
}

export function consolidateLanguages(resourcesMap, currentLanguage) {
  const defaultLanguages = Object.keys(resourcesMap).map(jid => resourcesMap[jid].defaultLanguage);

  let languages = [];
  Object.keys(resourcesMap).forEach(jid => {
    languages = [
      ...languages,
      ...Object.keys(resourcesMap[jid].titlesByLanguage || {}),
      ...(resourcesMap[jid].languages || []),
    ];
  });
  const uniqueLanguages = languages.filter((lang, idx) => languages.indexOf(lang) === idx);

  let defaultLanguage;
  if (uniqueLanguages.indexOf(currentLanguage) !== -1) {
    defaultLanguage = currentLanguage;
  } else {
    defaultLanguage = getMostCommonElement(defaultLanguages);
  }

  return { defaultLanguage, uniqueLanguages };
}

function getMostCommonElement(arr) {
  let freqs = {};
  for (let el of arr) {
    if (el in freqs) {
      freqs[el]++;
    } else {
      freqs[el] = 0;
    }
  }

  let res = arr[0];
  let freq = 0;
  for (let el of arr) {
    if (freqs[el] > freq || (freqs[el] === freq && el < res)) {
      freq = freqs[el];
      res = el;
    }
  }
  return res;
}
