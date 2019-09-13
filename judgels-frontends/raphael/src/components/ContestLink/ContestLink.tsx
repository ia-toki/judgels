import * as React from 'react';

import { Link } from 'react-router-dom';

export const ContestLink = ({ contest }) => <Link to={`/contests/${contest.slug}`}>{contest.name}</Link>;
