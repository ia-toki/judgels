import { Box } from '@blueprintjs/labs';

import { ContentCard } from '../ContentCard/ContentCard';
import RichStatementText from '../RichStatementText/RichStatementText';

import './LessonStatementCard.scss';

export function LessonStatementCard({ alias, statement }) {
  return (
    <ContentCard className="lesson-statement">
      <h2 className="lesson-statement__name">
        {alias && `${alias}. `}
        {statement.title}
      </h2>
      <Box className="lesson-statement__text" marginTop={6}>
        <RichStatementText key={alias}>{statement.text}</RichStatementText>
      </Box>
    </ContentCard>
  );
}
