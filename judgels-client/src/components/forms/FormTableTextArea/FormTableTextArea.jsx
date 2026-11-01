import { Classes } from '@blueprintjs/core';
import classNames from 'classnames';

import { FormTableInput } from '../FormTableInput/FormTableInput';
import { getIntentClassName } from '../meta';

export function FormTableTextArea(props) {
  const { input, meta } = props;
  return (
    <FormTableInput {...props}>
      <textarea {...input} className={classNames(Classes.INPUT, Classes.FILL, getIntentClassName(meta))} rows={5} />
    </FormTableInput>
  );
}
