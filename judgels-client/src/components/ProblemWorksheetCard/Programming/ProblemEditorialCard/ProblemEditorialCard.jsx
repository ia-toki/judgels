import { ContentCard } from '../../../ContentCard/ContentCard';
import RichStatementText from '../../../RichStatementText/RichStatementText';

export function ProblemEditorialCard({ alias, statement: { title }, editorial: { text }, titleSuffix }) {
  return (
    <ContentCard>
      <h2 className="programming-problem-statement__name">
        {alias ? `${alias}. ` : ''}
        {title}
        {titleSuffix}
      </h2>
      <div className="programming-problem-statement__text">
        <RichStatementText key={alias}>{text}</RichStatementText>
      </div>
    </ContentCard>
  );
}
