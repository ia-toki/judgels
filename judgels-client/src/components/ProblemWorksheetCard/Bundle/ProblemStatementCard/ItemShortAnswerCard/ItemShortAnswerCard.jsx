import { Card } from '@blueprintjs/core';
import { Box, Flex } from '@blueprintjs/labs';

import RichStatementText from '../../../../RichStatementText/RichStatementText';
import { AnswerState } from '../../itemStatement';
import ItemShortAnswerForm from './ItemShortAnswerForm/ItemShortAnswerForm';

export function ItemShortAnswerCard(props) {
  const { className, config, meta, initialAnswer, onSubmit, itemNumber } = props;
  return (
    <Card className={className}>
      <Flex alignItems="baseline" gap={1}>
        <div className="bundle-problem-statement-item__num">{itemNumber}.</div>
        <Box flexGrow={1}>
          <RichStatementText>{config.statement}</RichStatementText>
        </Box>
      </Flex>
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
