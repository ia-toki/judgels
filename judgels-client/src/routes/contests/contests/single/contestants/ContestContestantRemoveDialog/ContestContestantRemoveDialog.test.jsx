import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { ContestContestantRemoveDialog } from './ContestContestantRemoveDialog';

describe('ContestContestantRemoveDialog', () => {
  let onDeleteContestants;
  let wrapper;

  beforeEach(() => {
    onDeleteContestants = jest.fn().mockReturnValue(Promise.resolve({ deletedContestantProfilesMap: {} }));

    const store = configureMockStore()({});

    const props = {
      contest: { jid: 'contestJid' },
      onDeleteContestants,
    };
    wrapper = mount(
      <Provider store={store}>
        <ContestContestantRemoveDialog {...props} />
      </Provider>
    );
  });

  test('form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.prop('onChange')({ target: { value: 'andi\n\nbudi\n caca  \n' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onDeleteContestants).toHaveBeenCalledWith('contestJid', ['andi', 'budi', 'caca']);
  });
});
