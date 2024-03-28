import { ContentCard } from '../../../ContentCard/ContentCard';
import RichStatementText from '../../../RichStatementText/RichStatementText';

export function ProblemEditorialCard({ alias, statement: { title }, editorial: { text }, showTitle = true }) {
  return (
    <ContentCard>
      {showTitle && (
        <h2 className="programming-problem-statement__name">
          {alias ? `${alias}. ` : ''}
          {title}
        </h2>
      )}
      <div className="programming-problem-statement__text">
        <RichStatementText key={alias}>{text}</RichStatementText>
      </div>
    </ContentCard>
  );
}
