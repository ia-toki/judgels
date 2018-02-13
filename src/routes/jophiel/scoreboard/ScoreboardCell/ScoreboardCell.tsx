import * as React from 'react';

import './ScoreboardCell.css';

export interface ScoreboardElement {
  key: string;
  strong: string;
  small: string;
  theme: string;
}

interface ScoreboardCellProps {
  element: ScoreboardElement;
}

export const ScoreboardCell = (props: ScoreboardCellProps) => {
  return (
    <td className={props.element.theme}>
      <strong>{props.element.strong}</strong>
      <br />
      <small>{props.element.small}</small>
    </td>
  );
};

export default ScoreboardCell;
