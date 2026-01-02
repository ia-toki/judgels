import { Link } from '@tanstack/react-router';

export function ContestLink({ contest }) {
  return <Link to={`/contests/${contest.slug}`}>{contest.name}</Link>;
}
