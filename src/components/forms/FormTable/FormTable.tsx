import * as React from 'react';

import './FormTable.css';

export interface FormTableRow {
  key: string;
  title: string;
  value?: string;
}

export interface FormTableProps {
  rows: FormTableRow[];
}

export const FormTable = (props: FormTableProps) => {
  const rows = props.rows.map(row => (
    <tr key={row.key}>
      <td className="form-table__title">{row.title}</td>
      <td data-key={row.key} className="form-table__value">
        {row.value}
      </td>
    </tr>
  ));

  return (
    <table className="pt-table pt-striped">
      <tbody>{rows}</tbody>
    </table>
  );
};
