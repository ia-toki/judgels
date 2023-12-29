import { Text } from '@blueprintjs/core';

import { ItemType } from '../../../../modules/api/sandalphon/problemBundle';

export function FormattedAnswer({ answer, type }) {
  if (!answer) {
    return '-';
  }
  if (type === ItemType.ShortAnswer) {
    return <Text ellipsize>{answer}</Text>;
  }
  if (type === ItemType.Essay) {
    return <pre>{answer}</pre>;
  }
  return answer;
}
