/**
** Formatea un string para que quede "Palabra" en vez de "PALABRA".
**/
export function capitalizarString(text){
	
	return text.toLowerCase().replace(/\b\w/g, (char) => char.toUpperCase());
}