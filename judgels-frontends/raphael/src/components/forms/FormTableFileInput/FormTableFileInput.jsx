import { FileInput } from '@blueprintjs/core';
import * as React from 'react';

import { FormTableInput } from '../FormTableInput/FormTableInput';

const handleChange = onChange => e => {
  e.preventDefault();
  const { target } = e;
  return onChange(target.files.length ? target.files[0] : undefined);
};

export function FormTableFileInput(props) {
  const { input, placeholder } = props;

  const { value, onChange, onBlur, ...inputProps } = input;
  return (
    <FormTableInput {...props}>
      <FileInput
        inputProps={{ onChange: handleChange(onChange), ...inputProps }}
        text={value ? value.name : placeholder || 'Choose file...'}
      />
    </FormTableInput>
  );
}
