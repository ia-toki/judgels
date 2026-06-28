import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../test/RouterWrapper';
import { nockApi } from '../../../../../utils/nock';
import InfoPage from './InfoPage';

describe('InfoPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'JIDUSER1' });
  });

  const renderComponent = async () => {
    nockApi().get('/users/JIDUSER1').reply(200, {
      jid: 'JIDUSER1',
      username: 'andi',
      email: 'andi@domain.com',
    });

    nockApi().get('/users/JIDUSER1/info').reply(200, {
      name: 'Andi',
      gender: 'MALE',
      country: 'ID',
      homeAddress: 'Address',
      shirtSize: 'M',
      institutionName: 'MIT',
      institutionCountry: 'US',
      institutionProvince: 'MA',
      institutionCity: 'Cambridge',
    });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter>
            <InfoPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('details', async () => {
    await renderComponent();

    await waitFor(() => expect(document.querySelector('[data-key="name"]').textContent).toEqual('Andi'));
    expect(document.querySelector('[data-key="country"]').textContent).toEqual('Indonesia');
    expect(document.querySelector('[data-key="institutionName"]').textContent).toEqual('MIT');
  });

  test('info form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    await user.click(await screen.findByRole('button', { name: /edit/i }));

    const name = screen.getByDisplayValue('Andi');
    await user.clear(name);
    await user.type(name, 'New Andi');

    nockApi()
      .put('/users/JIDUSER1/info', body => body.name === 'New Andi')
      .reply(200, {});

    await user.click(screen.getByRole('button', { name: /save changes/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
