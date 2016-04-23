<h1>Bienvenido al servicio de Noticias de Apretaste!</h1>
<p>Estamos añadiendo mas fuentes de noticias todos los días, abriendo Cuba al mundo.</p>

{space10}

<center>
<h2>Categorias para Noticias</h2>
<table border=1 width="400 px">
	<tr bgcolor="{cycle values="#eeeeee,#dddddd"}">
		<td width="150 px"><b>Categoria</b></td>
		<td width="200 px"><b>Descripción</b></td>
		<td width="50 px"><b>Articulos</b></td>
	</tr>

	{foreach $categories as $category}
		{strip}
		<tr bgcolor="{cycle values="#eeeeee,#dddddd"}">
			<td width="150 px">{link href="NOTICIAS {$category->title}" caption="{$category->title}"}</td>
			<td width="200 px">{$category->description}</td>
			<td width="50 px">{$category->articleCount}</td>
		</tr>
		{/strip}
	{/foreach}
</table>
</center>

{space15}
{space30}

<h1>Como Utilizar el Servicio de Noticias</h1>
<table>
	<tr>
		<td valign="top">
			<h2>Consulte Noticias del Mundo, directo desde Internet</h2>
			<p><b>1.</b> Cree un nuevo email. En la secci&oacute;n "Para" escriba: {apretaste_email}</p>
			<p><b>2.</b> En la secci&oacute;n "Asunto" escriba: <span style="color:green;">NOTICIAS</span> seguido del nombre de una Categoria de Noticias o dejalo en blanco para ver el catalogo completo de categorias</p>
			<p><b>3.</b> Env&iacute;e el email. En segundos recibir&aacute; otro email con la informaci&oacute;n pedida</p>
			{space10}
			<center>
				{button href="NOTICIAS Deportes" caption="Probar Noticias de Deportes"}
			</center>
		</td>
		<td valign="top">
			{emailbox title="Wikipedia" from="{$userEmail}" subject="NOTICIAS Deportes"}
		</td>
	</tr>
</table>

{space30}

<!--  https://tech.yandex.com/translate/doc/dg/concepts/design-requirements-docpage/ -->
Traducción en tiempo real por medio de Yandex. <a href="http://translate.yandex.com/">Powered by Yandex.Translate</a>
