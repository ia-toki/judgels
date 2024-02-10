import { Card } from '@blueprintjs/core';

import RichStatementText from '../../../../RichStatementText/RichStatementText';
import { AnswerState } from '../../itemStatement';
import ItemEssayForm from './ItemEssayForm/ItemEssayForm';

export function ItemEssayCard(props) {
  const { className, config, meta, initialAnswer, onSubmit, itemNumber } = props;
  return (
    <Card className={className}>
      <div className="bundle-problem-statement-item__statement">
        <div className="__item-num">{itemNumber}.</div>
        <div className="__item-statement">
          <RichStatementText>{config.statement}</RichStatementText>
        </div>
      </div>
      <ItemEssayForm
        initialAnswer={initialAnswer}
        onSubmit={onSubmit}
        meta={meta}
        {...props}
        answerState={initialAnswer ? AnswerState.AnswerSaved : AnswerState.NotAnswered}
      />
    </Card>
  );
}
