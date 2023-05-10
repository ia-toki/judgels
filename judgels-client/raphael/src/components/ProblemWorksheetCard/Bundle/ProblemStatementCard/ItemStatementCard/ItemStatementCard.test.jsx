import { mount } from 'enzyme';

import { ItemStatementCard } from './ItemStatementCard';
import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';

describe('ItemStatementCard', () => {
  let wrapper;

  const props = {
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
