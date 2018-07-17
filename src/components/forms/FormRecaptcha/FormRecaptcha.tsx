import * as React from 'react';
import ReCAPTCHA from 'react-google-recaptcha';

export const FormRecaptcha = props => <ReCAPTCHA sitekey={props.siteKey} onChange={props.input.onChange} />;
