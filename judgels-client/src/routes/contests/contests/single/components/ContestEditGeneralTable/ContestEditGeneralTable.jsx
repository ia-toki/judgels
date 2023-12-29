import { FormattedDuration } from '../../../../../../components/FormattedDuration/FormattedDuration';
import { FormTable } from '../../../../../../components/forms/FormTable/FormTable';
import { formatDateTime } from '../../../../../../utils/datetime';

export function ContestEditGeneralTable({ contest }) {
  const rows = [
    { key: 'jid', title: 'JID', value: contest.jid },
    { key: 'slug', title: 'Slug', value: contest.slug },
    { key: 'name', title: 'Name', value: contest.name },
    { key: 'style', title: 'Style', value: contest.style },
    { key: 'beginTime', title: 'Begin time', value: formatDateTime(new Date(contest.beginTime), true) },
    { key: 'duration', title: 'Duration', value: <FormattedDuration value={contest.duration} /> },
  ];

  return <FormTable rows={rows} />;
}
