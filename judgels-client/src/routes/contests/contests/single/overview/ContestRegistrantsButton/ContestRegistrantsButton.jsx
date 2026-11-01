import { Button } from '@blueprintjs/core';
import { People } from '@blueprintjs/icons';
import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { contestApprovedContestantsCountQueryOptions } from '../../../../../../modules/queries/contestContestant';
import { useSession } from '../../../../../../modules/session';
import ContestRegistrantsDialog from '../ContestRegistrantsDialog/ContestRegistrantsDialog';

export default function ContestRegistrantsButton() {
  const { contestSlug } = useParams({ strict: false });
  const { isLoggedIn } = useSession();
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const { data: contestantsCount } = useQuery({
    ...contestApprovedContestantsCountQueryOptions(contest.jid),
    enabled: isLoggedIn,
  });

  const [isDialogOpen, setIsDialogOpen] = useState(false);

  if (contestantsCount === undefined) {
    return null;
  }

  return (
    <>
      <Button
        small
        icon={<People />}
        text={`View registrants (${contestantsCount})`}
        onClick={() => setIsDialogOpen(true)}
      />
      {isDialogOpen && <ContestRegistrantsDialog onClose={() => setIsDialogOpen(false)} />}
    </>
  );
}
