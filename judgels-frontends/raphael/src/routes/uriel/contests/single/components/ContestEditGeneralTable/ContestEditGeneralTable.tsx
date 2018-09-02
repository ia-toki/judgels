import * as React from 'react';

import { FormTable, FormTableRow } from 'components/forms/FormTable/FormTable';
import { Contest } from 'modules/api/uriel/contest';

export interface ContestEditGeneralTableProps {
  contest: Contest;
}

export const ContestEditGeneralTable = (props: ContestEditGeneralTableProps) => {
  const { contest } = props;

  const rows: FormTableRow[] = [
    { key: 'jid', title: 'JID', value: contest.jid },
    { key: 'slug', title: 'Slug', value: contest.slug },
    { key: 'name', title: 'Name', value: contest.name },
  ];

  return <FormTable rows={rows} />;
};
