import { Divider } from '@blueprintjs/core';

import { ContentCard } from '../../../ContentCard/ContentCard';
import { HtmlText } from '../../../HtmlText/HtmlText';
import { ItemType } from '../../../../modules/api/sandalphon/problemBundle';

import { ItemStatementCard } from './ItemStatementCard/ItemStatementCard';
import { ItemMultipleChoiceCard } from './ItemMultipleChoiceCard/ItemMultipleChoiceCard';
import { ItemShortAnswerCard } from './ItemShortAnswerCard/ItemShortAnswerCard';
import { ItemEssayCard } from './ItemEssayCard/ItemEssayCard';

import './ProblemStatementCard.scss';

export function ProblemStatementCard({
  items,
  alias,
  statement,
  onAnswerItem,
  latestSubmissions,
  reasonNotAllowedToSubmit,
}) {
  const generateOnAnswer = itemJid => {
    return async answer => {
      return await onAnswerItem(itemJid, answer || '');
    };
  };

  const renderStatement = item => {
    return <ItemStatementCard className="bundle-problem-statement-item" key={item.meta} {...item} />;
  };

  const renderShortAnswer = item => {
    const latestAnswer = latestSubmissions[item.jid];
    const initialAnswer = latestAnswer && latestAnswer.answer;
    return (
      <ItemShortAnswerCard
        onSubmit={generateOnAnswer(item.jid)}
        className="bundle-problem-statement-item"
        key={item.meta}
        {...item}
        itemNumber={item.number}
        initialAnswer={initialAnswer}
        disabled={!!reasonNotAllowedToSubmit}
      />
    );
  };

  const renderEssay = item => {
    const latestAnswer = latestSubmissions[item.jid];
    const initialAnswer = latestAnswer && latestAnswer.answer;
    return (
      <ItemEssayCard
        onSubmit={generateOnAnswer(item.jid)}
        className="bundle-problem-statement-item"
        key={item.meta}
        {...item}
        itemNumber={item.number}
        initialAnswer={initialAnswer}
        disabled={!!reasonNotAllowedToSubmit}
      />
    );
  };

  const renderMultipleChoice = item => {
    const latestSub = latestSubmissions[item.jid];
    const initialAnswer = latestSub && latestSub.answer;
    return (
      <ItemMultipleChoiceCard
        onChoiceChange={generateOnAnswer(item.jid)}
        className="bundle-problem-statement-item"
        key={item.meta}
        {...item}
        itemNumber={item.number}
        initialAnswer={initialAnswer}
        disabled={!!reasonNotAllowedToSubmit}
      />
    );
  };

  return (
    <ContentCard>
      <h2 className="bundle-problem-statement__name">
        {alias ? `${alias}. ` : ''}
        {statement.title}
      </h2>
      <div className="bundle-problem-statement__text">
        <HtmlText>{statement.text}</HtmlText>
      </div>
      <Divider />
      {items.map(item => {
        switch (item.type) {
          case ItemType.MultipleChoice:
            return renderMultipleChoice(item);
          case ItemType.ShortAnswer:
            return renderShortAnswer(item);
          case ItemType.Essay:
            return renderEssay(item);
          default:
            return renderStatement(item);
        }
      })}
    </ContentCard>
  );
}
