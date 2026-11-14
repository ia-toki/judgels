import { act, render, screen } from '@testing-library/react';

import { userWebConfigQueryOptions } from '../../../../modules/queries/userWeb';
import { queryClient } from '../../../../modules/queryClient';
import { TestRouter } from '../../../../test/RouterWrapper';
import { SettingsTopbar } from './SettingsTopbar';

describe('SettingsTopbar', () => {
  afterEach(() => {
    queryClient.clear();
  });

  const renderComponent = async ({ trainingEnabled }) => {
    queryClient.setQueryData(userWebConfigQueryOptions().queryKey, { trainingEnabled });

    await act(async () =>
      render(
        <TestRouter>
          <SettingsTopbar activeItemPath="/admin/settings/general" />
        </TestRouter>
      )
    );
  };

  test('shows the Training tab when training is on', async () => {
    await renderComponent({ trainingEnabled: true });

    expect(await screen.findByText('General')).toBeInTheDocument();
    expect(screen.getByText('Training')).toBeInTheDocument();
  });

  test('hides the Training tab when training is off', async () => {
    await renderComponent({ trainingEnabled: false });

    expect(await screen.findByText('General')).toBeInTheDocument();
    expect(screen.queryByText('Training')).not.toBeInTheDocument();
  });
});
