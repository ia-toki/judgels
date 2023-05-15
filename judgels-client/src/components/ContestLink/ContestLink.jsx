import { Link } from 'react-router-dom';

export function ContestLink({ contest }) {
  return <Link to={`/contests/${contest.slug}`}>{contest.name}</Link>;
}
