import { ReactWrapper, mount } from 'enzyme';
import * as React from 'react';

import { ItemShortAnswerCard, ItemShortAnswerCardProps } from './ItemShortAnswerCard';
import { ItemShortAnswerConfig, ItemType } from '../../../../../modules/api/sandalphon/problemBundle';

describe('ItemShortAnswerCard', () => {
  let wrapper: ReactWrapper<ItemShortAnswerCard>;
  const itemConfig: ItemShortAnswerConfig = {
    statement: 'statement',
    score: 4,
    penalty: -2,
    inputValidationRegex: '/^d+$/',
    gradingRegex: '/^d+$/',
  };
  const props: ItemShortAnswerCardProps = {
    jid: 'jid',
    type: ItemType.Essay,
    meta: 'meta',
    config: itemConfig,
    disabled: false,
    onSubmit: jest.fn(),
    itemNumber: 1,
  };

  beforeEach(() => {
    wrapper = mount(<ItemShortAnswerCard {...props} />);
  });

  it('should render item statement', () => {
    const text = wrapper.find('div').map(div => div.text());
    expect(text).toContain('1.statement');
  });
});
