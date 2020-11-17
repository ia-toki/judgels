import { HTMLTable } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import './FormTable.css';

export interface FormTableRow {
  key: string;
  title: string;
  value?: any;
}

export interface FormTableProps {
  rows: FormTableRow[];
  small?: boolean;
  keyClassName?: string;
}

export const FormTable = (props: FormTableProps) => {
  const { small, keyClassName } = props;
  const rows = props.rows.map(row => (
    <tr key={row.key}>
      <td className={classNames(keyClassName, 'form-table__title', { 'form-table--small': small })}>{row.title}</td>
      <td data-key={row.key} className={classNames('form-table__value', { 'form-table--small': small })}>
        {row.value}
      </td>
    </tr>
  ));

  return (
    <HTMLTable striped>
      <tbody>{rows}</tbody>
    </HTMLTable>
  );
};
