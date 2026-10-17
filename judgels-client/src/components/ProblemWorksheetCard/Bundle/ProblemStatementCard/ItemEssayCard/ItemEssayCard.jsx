import { Card } from '@blueprintjs/core';
import { Box, Flex } from '@blueprintjs/labs';

import RichStatementText from '../../../../RichStatementText/RichStatementText';
import { AnswerState } from '../../itemStatement';
import ItemEssayForm from './ItemEssayForm/ItemEssayForm';

export function ItemEssayCard(props) {
  const { className, config, meta, initialAnswer, onSubmit, itemNumber } = props;
  return (
    <Card className={className}>
      <Flex alignItems="baseline" gap={1}>
        <div className="bundle-problem-statement-item__num">{itemNumber}.</div>
        <Box flexGrow={1}>
          <RichStatementText>{config.statement}</RichStatementText>
        </Box>
      </Flex>
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
