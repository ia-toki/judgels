import { ContentCard } from '../../../ContentCard/ContentCard';
import { KatexText } from '../../../KatexText/KatexText';

export function ProblemEditorialCard({ alias, statement: { title }, editorial: { text } }) {
  return (
    <ContentCard>
      <h2 className="programming-problem-statement__name">
        {alias ? `${alias}. ` : ''}
        {title}
      </h2>
      <div className="programming-problem-statement__text">
        <KatexText key={alias}>{text}</KatexText>
      </div>
    </ContentCard>
  );
}
