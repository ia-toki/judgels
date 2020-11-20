import * as React from 'react';

import { ContentCard } from '../ContentCard/ContentCard';
import { KatexText } from '../KatexText/KatexText';

import './LessonStatementCard.css';

export function LessonStatementCard({ alias, statement }) {
  return (
    <ContentCard className="lesson-statement">
      <h2 className="lesson-statement__name">
        {alias}. {statement.title}
      </h2>
      <div className="lesson-statement__text">
        <KatexText>{statement.text}</KatexText>
      </div>
    </ContentCard>
  );
}
