import { Intent, Tag } from '@blueprintjs/core';
import { ContentCard } from '../../../ContentCard/ContentCard';
import { KatexText } from '../../../KatexText/KatexText';

export function ProblemReviewCard({ alias, statement: { title }, editorial: { text } }) {
  return (
    <div class="programming-problem-worksheet">
      <ContentCard>
        <Tag intent={Intent.SUCCESS} style={{ width: '100%' }}></Tag>
        <h2 className="programming-problem-statement__name">
          {alias ? `${alias}. ` : ''}
          {title}
        </h2>

        <div className="programming-problem-statement__text">
          <KatexText key={alias}>{text}</KatexText>
        </div>
      </ContentCard>
    </div>
  );
}
