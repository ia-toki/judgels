import * as React from 'react';
import FlagIcon from 'react-flag-kit/lib/FlagIcon';

import { ScoreboardCell, ScoreboardElement } from '../../scoreboard/ScoreboardCell/ScoreboardCell';

import './Scoreboard.css';

export interface ScoreboardEntry {
  key: number;
  countryCode: string;
  username: string;
  elements: ScoreboardElement[];
}

interface ScoreboardProps {
  entries: ScoreboardEntry[];
}

export const Scoreboard = (props: ScoreboardProps) => {
  const elementHeads = props.entries[0].elements.map(element => <th key={element.key}>{element.key}</th>);
  const rows = props.entries.map(entry => {
    const elementCells = entry.elements.map(element => <ScoreboardCell key={element.key} element={element} />);
    return (
      <tr key={entry.key}>
        <td>{entry.key}</td>
        <td>
          <FlagIcon code={entry.countryCode} /> {entry.username}
        </td>
        {elementCells}
      </tr>
    );
  });

  return (
    <table className="pt-table pt-striped scoreboard__content">
      <thead>
        <tr>
          <th>#</th>
          <th>Username</th>
          {elementHeads}
        </tr>
      </thead>
      <tbody>{rows}</tbody>
    </table>
  );
};

export default Scoreboard;
