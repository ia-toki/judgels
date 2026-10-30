import { Flex } from '@blueprintjs/labs';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { EditableSections } from '../../../../components/EditableSections/EditableSections';
import { problemBySlugQueryOptions } from '../../../../modules/queries/problem';
import ProblemGeneralSection from '../ProblemGeneralSection/ProblemGeneralSection';
import ProblemSettersPanel from '../ProblemSettersPanel/ProblemSettersPanel';
import ProblemTopicTagsPanel from '../ProblemTopicTagsPanel/ProblemTopicTagsPanel';

export default function ProblemGeneralPage() {
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  return (
    <EditableSections>
      <Flex flexDirection="column" gap={8}>
        <ProblemGeneralSection problemJid={problemJid} />
        <ProblemSettersPanel problemJid={problemJid} />
        <ProblemTopicTagsPanel problemJid={problemJid} />
      </Flex>
    </EditableSections>
  );
}
