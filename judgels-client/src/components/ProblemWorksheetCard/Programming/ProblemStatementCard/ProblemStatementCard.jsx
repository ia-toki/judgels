import { HTMLTable } from '@blueprintjs/core';

import { ContentCard } from '../../../ContentCard/ContentCard';
import RichStatementText from '../../../RichStatementText/RichStatementText';

import './ProblemStatementCard.scss';

export function ProblemStatementCard({ alias, statement: { title, text }, limits: { timeLimit, memoryLimit } }) {
  const renderTimeLimit = timeLimit => {
    if (!timeLimit) {
      return '-';
    }
    if (timeLimit % 1000 === 0) {
      return timeLimit / 1000 + ' s';
    }
    return timeLimit + ' ms';
  };

  const renderMemoryLimit = memoryLimit => {
    if (!memoryLimit) {
      return '-';
    }
    if (memoryLimit % 1024 === 0) {
      return memoryLimit / 1024 + ' MB';
    }
    return memoryLimit + ' KB';
  };

  return (
    <ContentCard>
      <h2 className="programming-problem-statement__name">
        {alias ? `${alias}. ` : ''}
        {title}
      </h2>
      <HTMLTable compact className="programming-problem-statement__limits">
        <tbody>
          <tr>
            <td>Time limit</td>
            <td>{renderTimeLimit(timeLimit)}</td>
          </tr>
          <tr>
            <td>Memory limit</td>
            <td>{renderMemoryLimit(memoryLimit)}</td>
          </tr>
        </tbody>
      </HTMLTable>
      <div className="programming-problem-statement__text">
        <RichStatementText key={alias}>{text}</RichStatementText>
      </div>
    </ContentCard>
  );
}
