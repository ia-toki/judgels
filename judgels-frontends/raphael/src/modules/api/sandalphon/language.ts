// extracted from https://github.com/anurbol/languages-iso-639-1-2-3-json/blob/master/data.json
import { ProblemInfo } from './problem';

export const statementLanguageNamesMap = {
  ab: 'Abkhaz',
  aa: 'Afar',
  af: 'Afrikaans',
  ak: 'Akan',
  sq: 'Albanian',
  am: 'Amharic',
  ar: 'Arabic',
  an: 'Aragonese',
  hy: 'Armenian',
  as: 'Assamese',
  av: 'Avaric',
  ae: 'Avestan',
  ay: 'Aymara',
  az: 'Azerbaijani',
  bm: 'Bambara',
  ba: 'Bashkir',
  eu: 'Basque',
  be: 'Belarusian',
  bn: 'Bengali',
  bh: 'Bihari',
  bi: 'Bislama',
  bs: 'Bosnian',
  br: 'Breton',
  bg: 'Bulgarian',
  my: 'Burmese',
  ca: 'Catalan',
  ch: 'Chamorro',
  ce: 'Chechen',
  ny: 'Chichewa',
  zh: 'Chinese',
  cv: 'Chuvash',
  kw: 'Cornish',
  co: 'Corsican',
  cr: 'Cree',
  hr: 'Croatian',
  cs: 'Czech',
  da: 'Danish',
  dv: 'Divehi',
  nl: 'Dutch',
  dz: 'Dzongkha',
  en: 'English',
  eo: 'Esperanto',
  et: 'Estonian',
  ee: 'Ewe',
  fo: 'Faroese',
  fj: 'Fijian',
  fi: 'Finnish',
  fr: 'French',
  ff: 'Fula',
  gl: 'Galician',
  ka: 'Georgian',
  de: 'German',
  el: 'Greek',
  gn: 'Guaraní',
  gu: 'Gujarati',
  ht: 'Haitian',
  ha: 'Hausa',
  he: 'Hebrew',
  hz: 'Herero',
  hi: 'Hindi',
  ho: 'Hiri Motu',
  hu: 'Hungarian',
  ia: 'Interlingua',
  id: 'Indonesian',
  ie: 'Interlingue',
  ga: 'Irish',
  ig: 'Igbo',
  ik: 'Inupiaq',
  io: 'Ido',
  is: 'Icelandic',
  it: 'Italian',
  iu: 'Inuktitut',
  ja: 'Japanese',
  jv: 'Javanese',
  kl: 'Kalaallisut',
  kn: 'Kannada',
  kr: 'Kanuri',
  ks: 'Kashmiri',
  kk: 'Kazakh',
  km: 'Khmer',
  ki: 'Kikuyu',
  rw: 'Kinyarwanda',
  ky: 'Kyrgyz',
  kv: 'Komi',
  kg: 'Kongo',
  ko: 'Korean',
  ku: 'Kurdish',
  kj: 'Kwanyama',
  la: 'Latin',
  lb: 'Luxembourgish',
  lg: 'Ganda',
  li: 'Limburgish',
  ln: 'Lingala',
  lo: 'Lao',
  lt: 'Lithuanian',
  lu: 'Luba-Katanga',
  lv: 'Latvian',
  gv: 'Manx',
  mk: 'Macedonian',
  mg: 'Malagasy',
  ms: 'Malay',
  ml: 'Malayalam',
  mt: 'Maltese',
  mi: 'Māori',
  mr: 'Marathi',
  mh: 'Marshallese',
  mn: 'Mongolian',
  na: 'Nauru',
  nv: 'Navajo',
  nd: 'Northern Ndebele',
  ne: 'Nepali',
  ng: 'Ndonga',
  nb: 'Norwegian Bokmål',
  nn: 'Norwegian Nynorsk',
  no: 'Norwegian',
  ii: 'Nuosu',
  nr: 'Southern Ndebele',
  oc: 'Occitan',
  oj: 'Ojibwe',
  cu: 'Old Church Slavonic',
  om: 'Oromo',
  or: 'Oriya',
  os: 'Ossetian',
  pa: 'Panjabi',
  pi: 'Pāli',
  fa: 'Persian',
  pl: 'Polish',
  ps: 'Pashto',
  pt: 'Portuguese',
  qu: 'Quechua',
  rm: 'Romansh',
  rn: 'Kirundi',
  ro: 'Romanian',
  ru: 'Russian',
  sa: 'Sanskrit',
  sc: 'Sardinian',
  sd: 'Sindhi',
  se: 'Northern Sami',
  sm: 'Samoan',
  sg: 'Sango',
  sr: 'Serbian',
  gd: 'Gaelic',
  sn: 'Shona',
  si: 'Sinhala',
  sk: 'Slovak',
  sl: 'Slovene',
  so: 'Somali',
  st: 'Southern Sotho',
  es: 'Spanish',
  su: 'Sundanese',
  sw: 'Swahili',
  ss: 'Swati',
  sv: 'Swedish',
  ta: 'Tamil',
  te: 'Telugu',
  tg: 'Tajik',
  th: 'Thai',
  ti: 'Tigrinya',
  bo: 'Tibetan Standard',
  tk: 'Turkmen',
  tl: 'Tagalog',
  tn: 'Tswana',
  to: 'Tonga',
  tr: 'Turkish',
  ts: 'Tsonga',
  tt: 'Tatar',
  tw: 'Twi',
  ty: 'Tahitian',
  ug: 'Uyghur',
  uk: 'Ukrainian',
  ur: 'Urdu',
  uz: 'Uzbek',
  ve: 'Venda',
  vi: 'Vietnamese',
  vo: 'Volapük',
  wa: 'Walloon',
  cy: 'Welsh',
  wo: 'Wolof',
  fy: 'Western Frisian',
  xh: 'Xhosa',
  yi: 'Yiddish',
  yo: 'Yoruba',
  za: 'Zhuang',
  zu: 'Zulu',
};

export const statementLanguageDisplayNamesMap = Object.assign(
  {},
  ...Object.keys(statementLanguageNamesMap).map(code => ({
    [code]: statementLanguageNamesMap[code] + ' (' + code + ')',
  }))
);

export function sortLanguagesByName(languages: string[]) {
  return languages.slice().sort((a, b) => {
    const nameA = statementLanguageNamesMap[a];
    const nameB = statementLanguageNamesMap[b];
    return nameA < nameB ? -1 : nameA > nameB ? 1 : 0;
  });
}

export function consolidateLanguages(problemsMap: { [jid: string]: ProblemInfo }, currentLanguage: string) {
  const defaultLanguages = Object.keys(problemsMap).map(jid => problemsMap[jid].defaultLanguage);

  let languages: string[] = [];
  Object.keys(problemsMap).forEach(jid => {
    languages = [...languages, ...Object.keys(problemsMap[jid].titlesByLanguage)];
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

function getMostCommonElement(arr: any[]) {
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
