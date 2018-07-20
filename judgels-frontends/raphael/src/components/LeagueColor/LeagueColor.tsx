import * as React from 'react';

import { getRatingLeague } from '../../modules/api/jophiel/userRating';

import './LeagueColor.css';

interface LeagueColorProps {
  rating?: number;
  children: any;
}

export const LeagueColor = (props: LeagueColorProps) => (
  <span className={`league-${getRatingLeague()}`}>{props.children}</span>
);
