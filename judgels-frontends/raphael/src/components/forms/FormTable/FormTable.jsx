import { HTMLTable } from '@blueprintjs/core';
import classNames from 'classnames';

import './FormTable.css';

export function FormTable({ rows, small, keyClassName }) {
  const htmlRows = rows.map(({ key, title, value }) => (
    <tr key={key}>
      <td className={classNames(keyClassName, 'form-table__title', { 'form-table--small': small })}>{title}</td>
      <td data-key={key} className={classNames('form-table__value', { 'form-table--small': small })}>
        {value}
      </td>
    </tr>
  ));

  return (
    <HTMLTable striped>
      <tbody>{htmlRows}</tbody>
    </HTMLTable>
  );
}
