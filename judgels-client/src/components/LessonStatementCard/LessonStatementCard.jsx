import { ContentCard } from '../ContentCard/ContentCard';
import RichStatementText from '../RichStatementText/RichStatementText';

import './LessonStatementCard.scss';

export function LessonStatementCard({ alias, statement }) {
  return (
    <ContentCard className="lesson-statement">
      <h2 className="lesson-statement__name">
        {alias}. {statement.title}
      </h2>
      <div className="lesson-statement__text">
        <RichStatementText key={alias}>{statement.text}</RichStatementText>
      </div>
    </ContentCard>
  );
}
