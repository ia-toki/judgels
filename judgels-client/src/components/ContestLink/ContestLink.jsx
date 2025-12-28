import { Link } from 'react-router';

export function ContestLink({ contest }) {
  return <Link to={`/contests/${contest.slug}`}>{contest.name}</Link>;
}
