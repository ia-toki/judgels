import * as React from 'react';
import ReCAPTCHA from 'react-google-recaptcha';

export function FormRecaptcha({ siteKey, input }) {
  return <ReCAPTCHA sitekey={siteKey} onChange={input.onChange} />;
}
