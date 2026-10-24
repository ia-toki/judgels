import { Flex } from '@blueprintjs/labs';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { Link, useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ItemEssayCard } from '../../../../components/ProblemWorksheetCard/Bundle/ProblemStatementCard/ItemEssayCard/ItemEssayCard';
import { ItemMultipleChoiceCard } from '../../../../components/ProblemWorksheetCard/Bundle/ProblemStatementCard/ItemMultipleChoiceCard/ItemMultipleChoiceCard';
import { ItemShortAnswerCard } from '../../../../components/ProblemWorksheetCard/Bundle/ProblemStatementCard/ItemShortAnswerCard/ItemShortAnswerCard';
import { ItemStatementCard } from '../../../../components/ProblemWorksheetCard/Bundle/ProblemStatementCard/ItemStatementCard/ItemStatementCard';
import { ItemType, itemTypeNamesMap } from '../../../../modules/api/problemBundle';
import { formatItemMediaUrls } from '../../../../modules/api/problemItem';
import { problemBySlugQueryOptions, problemPermissionsQueryOptions } from '../../../../modules/queries/problem';
import { problemItemQueryOptions, updateProblemItemMutationOptions } from '../../../../modules/queries/problemItem';
import { problemStatementLanguagesQueryOptions } from '../../../../modules/queries/problemStatement';
import LanguageSelect from '../../catalog/LanguageSelect/LanguageSelect';
import ProblemItemEditForm from '../ProblemItemEditForm/ProblemItemEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

import '../../../../components/ProblemWorksheetCard/Bundle/ProblemStatementCard/ProblemStatementCard.scss';

const itemCardsMap = {
  [ItemType.Statement]: ItemStatementCard,
  [ItemType.MultipleChoice]: ItemMultipleChoiceCard,
  [ItemType.ShortAnswer]: ItemShortAnswerCard,
  [ItemType.Essay]: ItemEssayCard,
};

export default function ProblemItemPage() {
  const { problemSlug, itemJid } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const {
    data: { canEdit },
  } = useSuspenseQuery(problemPermissionsQueryOptions(problemJid));

  const {
    data: { enabledLanguages, defaultLanguage },
  } = useSuspenseQuery(problemStatementLanguagesQueryOptions(problemJid));

  const [selectedLanguage, setSelectedLanguage] = useState(defaultLanguage);
  const language = enabledLanguages.includes(selectedLanguage) ? selectedLanguage : defaultLanguage;

  const { data: item } = useQuery(problemItemQueryOptions(problemJid, itemJid, { language }));

  const updateItemMutation = useMutation(updateProblemItemMutationOptions(problemJid, itemJid, language));

  const updateItem = async data => {
    await updateItemMutation.mutateAsync(data, {
      onSuccess: () => toastActions.showSuccessToast('Item updated.'),
    });
  };

  const renderTitle = () => {
    if (!item) {
      return 'Item';
    }
    const name = `${itemTypeNamesMap[item.type]} item`;
    return item.number ? `${name} (no. ${item.number})` : name;
  };

  const renderLanguageSelect = () => {
    return <LanguageSelect languages={enabledLanguages} value={language} onChange={setSelectedLanguage} />;
  };

  const renderContent = () => {
    if (!item) {
      return <LoadingState />;
    }
    if (canEdit) {
      // The form holds its own edits, so it starts over whenever the language or the saved item changes.
      return <ProblemItemEditForm key={JSON.stringify([language, item])} item={item} onSubmit={updateItem} />;
    }

    const ItemCard = itemCardsMap[item.type];
    return <ItemCard {...formatItemMediaUrls(item, problemJid)} itemNumber={item.number} disabled />;
  };

  return (
    <div>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <BreadcrumbTitle
            parent={<Link to={`/admin/problems/${problemSlug}/items`}>Items</Link>}
            current={renderTitle()}
          />
          {renderLanguageSelect()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
