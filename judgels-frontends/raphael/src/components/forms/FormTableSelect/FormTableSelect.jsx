import { HTMLSelect } from '@blueprintjs/core';
import classNames from 'classnames';

import { getIntentClassName } from '../meta';
import { FormTableInput } from '../FormTableInput/FormTableInput';

import './FormTableSelect.scss';

export function FormTableSelect(props) {
  const { input, meta, children } = props;
  return (
    <FormTableInput {...props}>
      <HTMLSelect {...input} className={classNames(getIntentClassName(meta))}>
        {children}
      </HTMLSelect>
    </FormTableInput>
  );
}
