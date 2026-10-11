import { PartnerPermission } from '../../../../modules/api/problemPartner';

const MAX_PARTNERS = 100;

export function toCsv(partners, profilesMap) {
  return partners.map(partner => `${profilesMap[partner.userJid]?.username},${partner.permission}`).join('\n');
}

// Returns { partners } as the endpoint takes them, or { error } when the CSV is invalid.
export function parseCsv(csv) {
  const partners = [];
  const usernames = new Set();

  for (const line of (csv || '').split('\n')) {
    const row = line.trim();
    if (!row) {
      continue;
    }

    const tokens = row.split(',').map(token => token.trim());
    const [username, permission = PartnerPermission.Update] = tokens;
    if (tokens.length > 2 || !username) {
      return { error: `Invalid row: ${row}` };
    }
    if (!Object.values(PartnerPermission).includes(permission)) {
      return { error: `Invalid permission: ${permission}` };
    }
    if (usernames.has(username)) {
      return { error: `Duplicate user: ${username}` };
    }

    usernames.add(username);
    partners.push({ username, permission });
  }

  if (partners.length > MAX_PARTNERS) {
    return { error: `At most ${MAX_PARTNERS} partners are allowed` };
  }
  return { partners };
}
