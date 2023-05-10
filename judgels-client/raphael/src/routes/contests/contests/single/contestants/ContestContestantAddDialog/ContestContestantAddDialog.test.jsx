import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { ContestContestantAddDialog } from './ContestContestantAddDialog';

describe('ContestContestantAddDialog', () => {
  let onUpsertContestants;
  let wrapper;

  beforeEach(() => {
    onUpsertContestants = jest
      .fn()
      .mockReturnValue(Promise.resolve({ insertedContestantProfilesMap: {}, alreadyContestantProfilesMap: {} }));

    const store = configureMockStore()({});

    const props = {
      contest: { jid: 'contestJid' },
      onUpsertContestants,
    };
    wrapper = mount(
      <Provider store={store}>
        <ContestContestantAddDialog {...props} />
      </Provider>
    );
  });

  test('form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.getDOMNode().value = 'andi\n\nbudi\n caca  \n';
    usernames.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpsertContestants).toHaveBeenCalledWith('contestJid', ['andi', 'budi', 'caca']);
  });
});
