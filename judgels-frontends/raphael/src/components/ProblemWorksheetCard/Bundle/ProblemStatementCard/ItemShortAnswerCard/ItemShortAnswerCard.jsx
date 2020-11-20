import { Card } from '@blueprintjs/core';
import * as React from 'react';

import { HtmlText } from '../../../../HtmlText/HtmlText';
import { AnswerState } from '../../itemStatement';
import ItemShortAnswerForm from './ItemShortAnswerForm/ItemShortAnswerForm';

export function ItemShortAnswerCard(props) {
  const { className, config, meta, initialAnswer, onSubmit, itemNumber } = props;
  return (
    <Card className={className}>
      <div className="bundle-problem-statement-item__statement">
        <div className="__item-num">{itemNumber}.</div>
        <div className="__item-statement">
          <HtmlText>{config.statement}</HtmlText>
        </div>
      </div>
      <ItemShortAnswerForm
        initialAnswer={initialAnswer}
        onSubmit={onSubmit}
        meta={meta}
        {...props}
        answerState={initialAnswer ? AnswerState.AnswerSaved : AnswerState.NotAnswered}
      />
    </Card>
  );
}
