import { useQuery } from '@tanstack/react-query';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { userRolesQueryOptions } from '../../../../modules/queries/userRole';
import { RolesSection } from '../RolesSection/RolesSection';

export default function RolesPage() {
  const { data: response } = useQuery(userRolesQueryOptions());

  return <ContentCard title="Roles">{response ? <RolesSection roles={response} /> : <LoadingState />}</ContentCard>;
}
