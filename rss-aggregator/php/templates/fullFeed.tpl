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

{space10}

<center>
<h4>Todos los articulos para {$feed->title}, ofrecido por {$feed->sourceName}</h4>
<h5>Haz un click sobre el nombre de un Articulo para verlo completo</h5>
<table border=1 width="650 px">
	<tr bgcolor="{cycle values="#eeeeee,#dddddd"}">
		<td width="175 px"><b>Fuente</b></td>
		<td width="475 px"><b>Description</b></td>
	</tr>
	<tr bgcolor="{cycle values="#eeeeee,#dddddd"}">
		<td width="175 px">{$feed->title}</td>
		<td width="475 px">{$feed->description}</td>
	</tr>
	<tr bgcolor="{cycle values="#eeeeee,#dddddd"}">
		<td colspan="2"><b>Articulos ({$feed->articleCount})</b></td>
	</tr>

		<tr bgcolor="{cycle values="#FFFFFF,#EEEEEE"}">
			<td colspan="2">
					{$counter=1}
					{foreach $feed->articles as $article}
						{strip}
						<table width="100%" border=1>
							<tr>
								{if $article->trimmed or $counter>=40}
									<td width="80%">{$counter++}.&nbsp;{link href="NOTICIAS >__{urlencode($article->guid)}" caption="{$article->title}"}</td>
								{else}
									<td width="80%">{$counter++}.&nbsp;<b>{$article->title}</b></td>
								{/if}
								<td width="20%"><nobr>{date("d/m/Y H:i:s", $article->pubDate/1000)}</nobr></td>
							</tr>
							{if $counter <= 40}
								<tr>
									<td colspan="2">{$article->description}</td>
								</tr>
								<tr>
									<td colspan="2">
									<table width="100%">
										<tr>
											<td align="left" width="50%">
											{if $feed->language and $feed->language!="es"}
												{link href="NOTICIAS >>_{urlencode($article->link)}" caption="Leer Articulo en su idioma original"}
											{/if}
											&nbsp;</td>
											<td align="right" width="50%">
											{if $article->trimmed}
												{link href="NOTICIAS >__{urlencode($article->link)}" caption="Leer Articulo Completo"}
											{else}
												{link href="WEB {urlencode($article->link)}" caption="Leer Pagina de la Noticia"}
											{/if}
											&nbsp;</td>
										</tr>
									</table>
									</td>
								</tr>
							{/if}
						</table>
						<hr>
						{/strip}
					{/foreach}
			</td>
		</tr>
</table>
</center>

{space15}

{space30}

<!--  https://tech.yandex.com/translate/doc/dg/concepts/design-requirements-docpage/ -->
Traducción en tiempo real por medio de Yandex. <a href="http://translate.yandex.com/">Powered by Yandex.Translate</a>

