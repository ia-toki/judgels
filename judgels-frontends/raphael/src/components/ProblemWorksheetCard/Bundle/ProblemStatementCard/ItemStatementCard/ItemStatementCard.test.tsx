import { ReactWrapper, mount } from 'enzyme';
import * as React from 'react';

import { ItemStatementCardProps, ItemStatementCard } from './ItemStatementCard';
import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';

describe('ItemStatementCard', () => {
  let wrapper: ReactWrapper<ItemStatementCardProps>;

  const props: ItemStatementCardProps = {
    jid: 'jid',
    type: ItemType.Statement,
    meta: 'meta',
    config: {
      statement: 'statement',
    },
    disabled: false,
  };

  beforeEach(() => {
    wrapper = mount(<ItemStatementCard {...props} />);
  });

  it('should render item statement', () => {
    const statement = wrapper.find('div').map(div => div.text());
    expect(statement).toContain(props.config.statement);
  });
});
