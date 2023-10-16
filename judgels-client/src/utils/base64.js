// https://developer.mozilla.org/en-US/docs/Glossary/Base64#the_unicode_problem
export function decodeBase64(base64) {
  const binString = atob(base64);
  return new TextDecoder().decode(Uint8Array.from(binString, m => m.codePointAt(0)));
}
